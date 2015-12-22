package de.cineaste.android.exportImport;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.Movie;

public class ExportImport {

    private static final Gson gson = new Gson();

    public static void exportMovies( List<Movie> movies ) {

        File exportPath = new File( Environment.getExternalStorageDirectory() + "/Cineaste" );

        if( !exportPath.exists() ) {
            exportPath.mkdir();
        }

        File exportFile = new File(
                Environment.getExternalStorageDirectory() + "/Cineaste/movies.json" );

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter( exportFile );
            fileWriter.write( gson.toJson( movies ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch ( Exception e ) {
                //die silently
            }
        }
    }

    public static List<Movie> importMovies( ) {

        final File importFile = new File(
                Environment.getExternalStorageDirectory() + "/Cineaste/movies.json" );
        try {
            FileReader fileReader = new FileReader( importFile );
            BufferedReader reader = new BufferedReader( fileReader );

            String temp;
            StringBuilder text = new StringBuilder();
            while ( (temp = reader.readLine()) != null ) {
                text.append( temp );
            }
            Type listType = new TypeToken<List<Movie>>() {
            }.getType();
            return gson.fromJson( text.toString(), listType );
        } catch ( Exception ex ) {
            return new ArrayList<>();
        }
    }
}