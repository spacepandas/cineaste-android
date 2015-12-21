package de.cineaste.android.entity;

public class MatchingResult extends MovieDto {

    private int counter;

    public MatchingResult( long id, String posterPath, String title, int counter ) {
        super( id, posterPath, title );
        this.counter = counter;
    }

    public MatchingResult( MovieDto movieDto, int counter ) {
        super( movieDto.getId(), movieDto.getPosterPath(), movieDto.getTitle() );
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter( int counter ) {
        this.counter = counter;
    }
}
