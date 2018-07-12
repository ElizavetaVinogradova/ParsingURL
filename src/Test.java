import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String userName = "root";
        String password = "root";
        String connectionUrl = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
        Class.forName("com.mysql.jdbc.Driver");
        try(Connection connection = DriverManager.getConnection(connectionUrl, userName, password)){
            System.out.println("CONNECTED");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //---------------------------------------------------------------------------------------------
        Document doc = Jsoup.connect("https://www.apple.com/ru/itunes/charts/movies/").get(); //получили общую страницу, распарсили в Document
        Elements h3Elements = doc.getElementsByTag("h3"); //получили по тегу список ссылок на все фильмы с первоначальной страницы


        /**int id, String filmUrl, String title, String shortDescription, String fullDescription, String imageUrl, String ageRating,
         ArrayList<String> actors, ArrayList<String> directors, ArrayList<String> producers, ArrayList<String> screenwriter **/
        List<Film> films = h3Elements.stream().map(h3Element -> {//берем каждый h3элемент
            Element aElement = h3Element.child(0); //из каждого h3элемента получаем дочерний узел, элемент <a>
            String url = aElement.attr("href"); //из <a> вытягиваем атрибут - ссылку на конкретный фильм
            //String title = aElement.child(0).text();
            Film film = new Film();
            try {
                System.out.println("==========================================");
                Document innerDoc = Jsoup.connect(url).get(); //по каждой отдельной ссылке получаем отдельный документ - фильм с описанием и т.д.
                film.setFilmUrl(url);
                System.out.println("FilmUrl " + film.getFilmUrl());
                film.setTitle(innerDoc.title());
                System.out.println("FilmTitle " + film.getTitle());


//---------------------------------------------------------------------

                //todo КОРОТКОЕ ОПИСАНИЕ
                /*Optional<Element> fullDescElement = innerDoc.getElementsByAttributeValue("itemprop", "description")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("href", "#").isEmpty();
                        })
                        .findFirst();
                film.setFullDescription(fullDescElement.get().getElementsByTag("a").text());
                System.out.println("What Description?  : " + film.getFullDescription());*/

//---------------------------------------------------------------------

                //todo ПОЛНОЕ ОПИСАНИЕ
                Optional<Element> fullDescElement = innerDoc.getElementsByAttributeValue("itemprop", "description")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("will-truncate-max-height", "270").isEmpty();
                        })
                        .findFirst();
                film.setFullDescription(fullDescElement.get().getElementsByTag("p").text());
                System.out.println("FullDescription: " + film.getFullDescription());

                //todo КАРТИНКИ
                Optional<Element> imgElement = innerDoc.getElementsByAttributeValue("class", "artwork")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("width", "227").isEmpty();
                        })
                        .findFirst();
                film.setImageUrl(imgElement.get().getElementsByTag("meta").first().attr("content"));
                System.out.println("ImageUrl - " + film.getImageUrl());

                //todo ВОЗРАСТ
                Optional<Element> ratingElement = innerDoc.getElementsByAttributeValue("class", "left")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("class", "content-rating").isEmpty();
                        })
                        .findFirst();
                film.setAgeRating(ratingElement.get().getElementsByAttributeValue("class", "content-rating").text());
                System.out.println("AgeRating - " + film.getAgeRating());

//-----------------------------------------------------------------------

                //todo АКТЁРЫ
                Elements liElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Актеры");
                List<String> actors = liElements.stream().map(liElement -> {
                    return liElement.getElementsByAttributeValue("itemprop", "name").text();
                }).collect(Collectors.toList());
                System.out.print("Актёры: ");
                film.setActors(actors);
                film.getActors().forEach(System.out::println);

//-----------------------------------------------------------------------

                //todo РЕЖИССЕРЫ
                Elements actorsElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Режиссер");
                List<String> directors = actorsElements.stream().map(liElement -> {
                    return liElement.getElementsByAttributeValue("itemprop", "name").text();
                }).collect(Collectors.toList());
                System.out.print("Режиссеры: ");
                film.setDirectors(directors);
                film.getDirectors().forEach(System.out::println);

//-----------------------------------------------------------------------

                //todo СЦЕНАРИСТЫ
                Elements screenwritersElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Сценарий");
                List<String> screenwriters = screenwritersElements.stream().map(liElement -> {
                    return liElement.getElementsByAttributeValue("itemprop", "name").text();
                }).collect(Collectors.toList());
                System.out.print("Сценаристы: ");
                film.setScreenwriter(screenwriters);
                film.getScreenwriter().forEach(System.out::println);

//-----------------------------------------------------------------------

                //todo ПРОДЮСЕРЫ
                Elements producersElements = innerDoc.getElementsByAttributeValue("metrics-loc", "Titledbox_Продюсеры");
                List<String> producers = producersElements.stream().map(liElement -> {
                    return liElement.getElementsByAttributeValue("itemprop", "name").text();
                }).collect(Collectors.toList());
                System.out.print("Продюсеры: ");
                film.setProducers(producers);
                film.getProducers().forEach(System.out::println);

//-----------------------------------------------------------------------
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Here`s JOHNY!");
            }

            return film;
        }).collect(Collectors.toList());
    }
}

//will-truncate-max-height="270"
//<span itemprop="description">
//<a href="#" class="more-link">...Еще</a>