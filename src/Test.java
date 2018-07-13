import com.mysql.fabric.jdbc.FabricMySQLDriver;
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

                    Optional<Element> nameElement = innerDoc.getElementsByTag("h1")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (nameElement != null && nameElement.isPresent()) {
                        film.setTitle(nameElement.get().getElementsByAttributeValue("itemprop", "name").text());
                    }else {film.setTitle("Не указано");}
                    //System.out.println("Title " + film.getTitle());

                    //todo ОПИСАНИЕ
                    Optional<Element> fullDescElement = innerDoc.getElementsByAttributeValue("itemprop", "description")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("will-truncate-max-height", "270").isEmpty();
                            })
                            .findFirst();
                    if (fullDescElement != null && fullDescElement.isPresent()) {
                        film.setFullDescription(fullDescElement.get().getElementsByTag("p").text());
                    }else {film.setFullDescription("Не указано");}
                    //System.out.println("FullDescription: " + film.getFullDescription());

                    //todo КАРТИНКИ
                    Optional<Element> imgElement = innerDoc.getElementsByAttributeValue("class", "artwork")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("width", "227").isEmpty();
                            })
                            .findFirst();
                    film.setImageUrl(imgElement.get().getElementsByTag("meta").first().attr("content"));
                    //System.out.println("ImageUrl - " + film.getImageUrl());

                    //todo ВОЗРАСТ
                    Optional<Element> ratingElement = innerDoc.getElementsByAttributeValue("class", "left")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("class", "content-rating").isEmpty();
                            })
                            .findFirst();
                    if (ratingElement != null && ratingElement.isPresent()) {film.setAgeRating(ratingElement.get().getElementsByAttributeValue("class", "content-rating").text());}
                    Optional<Element> rating2Element = innerDoc.getElementsByAttributeValue("class", "lockup-bundle-title")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("class", "content-rating").isEmpty();
                            })
                            .findFirst();
                    if (rating2Element != null && rating2Element.isPresent()) {film.setAgeRating(rating2Element.get().getElementsByAttributeValue("class", "content-rating").text());}
                    //System.out.println("AgeRating - " + film.getAgeRating());


                    //todo АКТЁРЫ
                    Optional<Element> actorsElement = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Актеры")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (actorsElement != null && actorsElement.isPresent()) {
                        film.setActors(actorsElement.get().getElementsByAttributeValue("itemprop", "name").text());
                    }else {film.setActors("Не указаны");}
                    //System.out.println("Actors - " + film.getActors());


                    //todo РЕЖИССЕРЫ
                    Optional<Element> directorsElement = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Режиссеры")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (directorsElement != null && directorsElement.isPresent()) {film.setProducers(directorsElement.get().getElementsByAttributeValue("itemprop", "name").text());}

                    Optional<Element> directorElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Режиссер")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (directorElements != null && directorElements.isPresent()) {film.setProducers(directorElements.get().getElementsByAttributeValue("itemprop", "name").text());}
                    //System.out.println("Продюсеры: " + film.getProducers());


                    //todo СЦЕНАРИСТЫ
                    Optional<Element> screenwritersElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Сценарий")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (screenwritersElements != null && screenwritersElements.isPresent()) {
                        film.setScreenwriter(screenwritersElements.get().getElementsByAttributeValue("itemprop", "name").text());
                    }else {film.setScreenwriter("Не указаны");}
                    //System.out.println("Сценаристы: " + film.getScreenwriter());


                    //todo ПРОДЮСЕРЫ
                    Optional<Element> producersElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Продюсеры")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (producersElements != null && producersElements.isPresent()) {film.setProducers(producersElements.get().getElementsByAttributeValue("itemprop", "name").text());}

                    Optional<Element> producerElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Продюсер")
                            .stream().filter(element -> {
                                return !element.getElementsByAttributeValue("itemprop", "name").isEmpty();
                            })
                            .findFirst();
                    if (producerElements != null && producerElements.isPresent()) {film.setProducers(producerElements.get().getElementsByAttributeValue("itemprop", "name").text());}
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
                preparedStatement.setString(4,film.getFullDescription());
                System.out.println("film.getFullDescription(): " + film.getFullDescription());
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
            System.out.println("Connecion failed");
            e.printStackTrace();
        }
    }
}