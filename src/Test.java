import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test {
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "root";
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String INSERT_NEW = "INSERT INTO films VALUES(?,?,?,?,?,?,?,?,?,?)";

    public static String parsingTitle(Document document, String tagName, String attribute, String attributeValue){
        Optional<Element> nameElement = document.getElementsByTag(tagName)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute, attributeValue).isEmpty();
                })
                .findFirst();
        if (nameElement != null && nameElement.isPresent()) {
            return nameElement.get().getElementsByAttributeValue(attribute, attributeValue).text();
        }else {return "Не указано";}
    }

    public static String parsingDescription(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2, String tagName){
        Optional<Element> fullDescElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        if (fullDescElement != null && fullDescElement.isPresent()) {
            return fullDescElement.get().getElementsByTag(tagName).text();
        }else {return "Не указано";}
    }

    public static String parsingIMG(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2, String tagName, String attributeKey){
        Optional<Element> imgElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        return imgElement.get().getElementsByTag(tagName).first().attr(attributeKey);
    }

    public static String parsingAgeAndDirectorsAndProducers(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2, String attribute3, String attributeValue3) {
        Optional<Element> ratingElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        if (ratingElement != null && ratingElement.isPresent()) {
           return ratingElement.get().getElementsByAttributeValue(attribute2, attributeValue2).text();
        }

        Optional<Element> rating2Element = document.getElementsByAttributeValue(attribute3, attributeValue3)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        if (rating2Element != null && rating2Element.isPresent()) {
            return rating2Element.get().getElementsByAttributeValue(attribute2, attributeValue2).text();
        }
        return "Не указано";
    }

    public static String parsingActorsAndScreenwriters(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2) {
        Optional<Element> actorsElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        if (actorsElement != null && actorsElement.isPresent()) {
            return actorsElement.get().getElementsByAttributeValue(attribute2, attributeValue2).text();
        }else {return "Не указаны";}
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException{

        Document doc = Jsoup.connect("https://www.apple.com/ru/itunes/charts/movies/").get(); //получили общую страницу, распарсили в Document
        Elements h3Elements = doc.getElementsByTag("h3"); //получили по тегу список ссылок на все фильмы с первоначальной страницы

        List<Film> films = h3Elements.stream().map(h3Element -> {//берем каждый h3элемент
            //System.out.println("childNodeSize" + h3Element.childNodeSize());
            Element aElement;
            try {
                aElement = h3Element.child(0); //из каждого h3элемента получаем дочерний узел, элемент <a>
                String url = aElement.attr("href"); //из <a> вытягиваем атрибут - ссылку на конкретный фильм
                Film film = new Film();
                try {
                    //System.out.println("==========================================");
                    Document innerDoc = Jsoup.connect(url).get(); //по каждой отдельной ссылке получаем отдельный документ - фильм с описанием и т.д.
                    film.setFilmUrl(url);
                    //System.out.println("FilmUrl " + film.getFilmUrl());
                    film.setTitle(parsingTitle(innerDoc, "h1", "itemprop", "name"));
                    //System.out.println("Title " + film.getTitle());
                    //todo ОПИСАНИЕ
                    film.setDescription(parsingDescription(innerDoc, "itemprop", "description", "will-truncate-max-height", "270", "p"));
                    //System.out.println("FullDescription: " + film.getDescription());
                    //todo КАРТИНКИ
                    film.setImageUrl(parsingIMG(innerDoc, "class", "artwork", "width", "227", "meta", "content"));
                    //System.out.println("ImageUrl - " + film.getImageUrl());
                    //todo ВОЗРАСТ
                    film.setAgeRating(parsingAgeAndDirectorsAndProducers(innerDoc, "class", "left", "class", "content-rating", "class", "lockup-bundle-title"));
                    //System.out.println("AgeRating - " + film.getAgeRating());
                    //todo АКТЁРЫ
                    film.setActors(parsingActorsAndScreenwriters(innerDoc, "metrics-loc", "Titledbox_Актеры", "itemprop", "name" ));
                    //System.out.println("Actors - " + film.getActors());
                    //todo РЕЖИССЕРЫ
                    film.setDirectors(parsingAgeAndDirectorsAndProducers(innerDoc, "metrics-loc", "Titledbox_Режиссеры", "itemprop", "name", "metrics-loc", "Titledbox_Режиссер"));
                    //System.out.println("Режиссеры: " + film.getDirectors());
                    //todo СЦЕНАРИСТЫ
                    film.setScreenwriter(parsingActorsAndScreenwriters(innerDoc, "metrics-loc", "Titledbox_Сценарий", "itemprop", "name"));
                    //System.out.println("Сценаристы: " + film.getScreenwriter());
                    //todo ПРОДЮСЕРЫ
                    film.setProducers(parsingAgeAndDirectorsAndProducers(innerDoc, "metrics-loc", "Titledbox_Продюсеры", "itemprop", "name", "metrics-loc", "Titledbox_Продюсер"));
                    //System.out.println("Продюсеры: " + film.getProducers());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Here`s JOHNY!");
                }
                return film;

            } catch (IndexOutOfBoundsException ignore) {
                return null;
            }

        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        try(Connection connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW)){
            int i=1;
            for (Film film : films) {
                preparedStatement.setInt(1,i++);
                preparedStatement.setString( 2,film.getFilmUrl());
                System.out.println("film.getTitle(): " + film.getTitle());
                preparedStatement.setString(3,film.getTitle());
                System.out.println("film.getFilmUrl(): " + film.getFilmUrl());
                preparedStatement.setString(4,film.getDescription());
                System.out.println("film.getDescription(): " + film.getDescription());
                preparedStatement.setString(5,film.getImageUrl());
                System.out.println("film.getImageUrl(): " + film.getImageUrl());
                preparedStatement.setString(6,film.getAgeRating());
                System.out.println("film.getAgeRating(): " + film.getAgeRating());
                preparedStatement.setString(7,film.getActors());
                System.out.println("film.getActors(): " + film.getActors());
                preparedStatement.setString(8,film.getDirectors());
                System.out.println("film.getDirectors(): " + film.getDirectors());
                preparedStatement.setString(9,film.getProducers());
                System.out.println("film.getProducers(): " + film.getProducers());
                preparedStatement.setString(10,film.getScreenwriter());
                System.out.println("film.getScreenwriter(): " + film.getScreenwriter());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }
    }
}