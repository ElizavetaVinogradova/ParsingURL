import java.util.ArrayList;
import java.util.List;

public class Film {
    private int id; //auto_increment
    private String filmUrl;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String imageUrl;
    private String ageRating;
    private List<String> actors;
    private List<String> directors;
    private List<String> producers;
    private List<String> screenwriter;

    public Film(String filmUrl, String title, String shortDescription, String fullDescription, String imageUrl, String ageRating,
                ArrayList<String> actors, ArrayList<String> directors, ArrayList<String> producers, ArrayList<String> screenwriter) {
        this.filmUrl = filmUrl;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.imageUrl = imageUrl;
        this.ageRating = ageRating;
        this.actors = actors;
        this.directors = directors;
        this.producers = producers;
        this.screenwriter = screenwriter;
    }

    public Film() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilmUrl() {
        return filmUrl;
    }

    public void setFilmUrl(String filmUrl) {
        this.filmUrl = filmUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }

    public List<String> getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(List<String> screenwriter) {
        this.screenwriter = screenwriter;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", filmUrl='" + filmUrl + '\'' +
                ", title='" + title + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", ageRating='" + ageRating + '\'' +
                ", actors=" + actors +
                ", directors=" + directors +
                ", producers=" + producers +
                ", screenwriter=" + screenwriter +
                '}';
    }
}
