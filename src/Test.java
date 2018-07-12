import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) throws IOException {
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

                //todo КОРОТКОЕ ОПИСАНИЕ!!!
                /*Optional<Element> fullDescElement = innerDoc.getElementsByAttributeValue("itemprop", "description")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("href", "#").isEmpty();
                        })
                        .findFirst();
                film.setFullDescription(fullDescElement.get().getElementsByTag("a").text());
                System.out.println("What Description?  : " + film.getFullDescription());*/

//---------------------------------------------------------------------

                //todo ПОЛНОЕ ОПИСАНИЕ!!!
                Optional<Element> fullDescElement = innerDoc.getElementsByAttributeValue("itemprop", "description")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("will-truncate-max-height", "270").isEmpty();
                        })
                        .findFirst();
                film.setFullDescription(fullDescElement.get().getElementsByTag("p").text());
                System.out.println("FullDescription: " + film.getFullDescription());

                //todo КАРТИНКИ!!!
                Optional<Element> imgElement = innerDoc.getElementsByAttributeValue("class", "artwork")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("width", "227").isEmpty();
                        })
                        .findFirst();
                film.setImageUrl(imgElement.get().getElementsByTag("meta").first().attr("content"));
                System.out.println("ImageUrl - " + film.getImageUrl());

                //todo ВОЗРАСТ!!!
                Optional<Element> ratingElement = innerDoc.getElementsByAttributeValue("class", "left")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("class", "content-rating").isEmpty();
                        })
                        .findFirst();
                film.setAgeRating(ratingElement.get().getElementsByAttributeValue("class", "content-rating").text());
                System.out.println("AgeRating - " + film.getAgeRating());

//-----------------------------------------------------------------------

                /*//todo АКТЁРЫ!!!
                Optional<Element> actorsElement = innerDoc.getElementsByTag("li")
                        .stream().filter(element -> {
                            return !element.getElementsByAttributeValue("itemtype", "http://schema.org/Person").isEmpty();
                        })
                        .findFirst();
                List<String> actorsList;
                film.setActors(actorsElement.get().getElementsByAttributeValue("itemprop", "name").text());
                System.out.println("Actors - " + film.getActors());*/

//-----------------------------------------------------------------------



               /*film.setDirectors();
                film.setProducers();
                film.setScreenwriter();*/
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