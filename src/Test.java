import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Objects;
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
                Film film = null;
                try {
                    //System.out.println("==========================================");
                    Document innerDoc = Jsoup.connect(url).get(); //по каждой отдельной ссылке получаем отдельный документ - фильм с описанием и т.д.
                    film = new Film(innerDoc, url);
                    film.setFilmUrl(url);
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
            for (Film film : films) {
                preparedStatement.setInt(1, film.getId());
                preparedStatement.setString( 2,film.getFilmUrl());
                preparedStatement.setString(3,film.getTitle());
                preparedStatement.setString(4,film.getDescription());
                preparedStatement.setString(5,film.getImageUrl());
                preparedStatement.setString(6,film.getAgeRating());
                preparedStatement.setString(7,film.getActors());
                preparedStatement.setString(8,film.getDirectors());
                preparedStatement.setString(9,film.getProducers());
                preparedStatement.setString(10,film.getScreenwriter());
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }
    }
}