package com.web.movierater.models.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

public class MovieDto {
    private static final String TITLE_EMPTY_ERROR = "Please enter a movie title.";
    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 300;
    private static final String TITLE_LENGTH_ERROR = "Please provide a title between 1 and 300 characters.";

    private static final int DIRECTOR_MIN_LENGTH = 1;
    private static final int DIRECTOR_MAX_LENGTH = 30;
    private static final String DIRECTOR_LENGTH_ERROR = "Please provide a director name between 1 and 30 characters.";

    private static final int YEAR_MIN = 1878;
    private static final int YEAR_MAX = 2025;
    private static final String YEAR_LENGTH_ERROR = "Please provide a release year between 1878 and present.";

    private static final String RATING_POSITIVE_ERROR = "Rating must be positive.";

    private int id;

    @NotNull(message = TITLE_EMPTY_ERROR)
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH,
            message = TITLE_LENGTH_ERROR)
    private String title;

    @Size(min = DIRECTOR_MIN_LENGTH, max = DIRECTOR_MAX_LENGTH,
            message = DIRECTOR_LENGTH_ERROR)
    private String director;

    @Min(value = YEAR_MIN, message = YEAR_LENGTH_ERROR)
    @Max(value = YEAR_MAX, message = YEAR_LENGTH_ERROR)
    private Integer releaseYear;

    @Column(name = "rating")
    @Positive(message = RATING_POSITIVE_ERROR)
    private Double rating;

    public MovieDto () {}

    public MovieDto(String title, String director, Integer releaseYear, Double rating) {
        this.setTitle(title);
        this.setDirector(director);
        this.setReleaseYear(releaseYear);
        this.setRating(rating);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String name) {
        this.title = name;
    }

    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
}
