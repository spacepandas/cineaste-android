package de.cineaste.android.entity;

public class MatchingResult extends MovieDto {

    private int counter;

    public MatchingResult( MovieDto movieDto, int counter ) {
        super( movieDto.getId(), movieDto.getPosterPath(), movieDto.getTitle() );
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
}
