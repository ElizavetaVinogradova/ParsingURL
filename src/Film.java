import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;

public class Film {
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

    public static String parsingBy2AttrValPairsAndTag(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2, String tagName, String attributeKey){
        Optional<Element> imgElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        return imgElement.get().getElementsByTag(tagName).first().attr(attributeKey);
    }

    public static String parsingBy3AttrValPairs(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2, String attribute3, String attributeValue3) {
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

    public static String parsingBy2AttrValPairs(Document document, String attribute1, String attributeValue1, String attribute2, String attributeValue2) {
        Optional<Element> actorsElement = document.getElementsByAttributeValue(attribute1, attributeValue1)
                .stream().filter(element -> {
                    return !element.getElementsByAttributeValue(attribute2, attributeValue2).isEmpty();
                })
                .findFirst();
        if (actorsElement != null && actorsElement.isPresent()) {
            return actorsElement.get().getElementsByAttributeValue(attribute2, attributeValue2).text();
        }else {return "Не указаны";}
    }



    private int id; //auto_increment
    private String filmUrl;
    private String title;
    private String description;
    private String imageUrl;
    private String ageRating;
    private String actors;
    private String directors;
    private String producers;
    private String screenwriter;


    public Film(Document doc, String filmUrl) {
        this.filmUrl = filmUrl;
        this.title = parsingTitle(doc, "h1", "itemprop", "name");
        this.description = parsingDescription(doc, "itemprop", "description", "will-truncate-max-height", "270", "p");
        this.imageUrl = parsingBy2AttrValPairsAndTag(doc, "class", "artwork", "width", "227", "meta", "content");
        this.ageRating = parsingBy3AttrValPairs(doc, "class", "left", "class", "content-rating", "class", "lockup-bundle-title");
        this.actors = parsingBy2AttrValPairs(doc, "metrics-loc", "Titledbox_Актеры", "itemprop", "name" );
        this.directors = parsingBy3AttrValPairs(doc, "metrics-loc", "Titledbox_Режиссеры", "itemprop", "name", "metrics-loc", "Titledbox_Режиссер");
        this.producers = parsingBy3AttrValPairs(doc, "metrics-loc", "Titledbox_Продюсеры", "itemprop", "name", "metrics-loc", "Titledbox_Продюсер");
        this.screenwriter = parsingBy2AttrValPairs(doc, "metrics-loc", "Titledbox_Сценарий", "itemprop", "name");
    }

    public int getId() {return id;}
    public String getFilmUrl() {
        return filmUrl;
    }
    public void setFilmUrl(String filmUrl) {
        this.filmUrl = filmUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getImageUrl() {return imageUrl;}
    public String getAgeRating() {return ageRating;}
    public String getActors() {return actors;}
    public String getDirectors() {return directors;}
    public String getProducers() {return producers;}
    public String getScreenwriter() {return screenwriter;}

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", filmUrl='" + filmUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", ageRating='" + ageRating + '\'' +
                ", actors=" + actors +
                ", directors=" + directors +
                ", producers=" + producers +
                ", screenwriter=" + screenwriter +
                '}';
    }
}
