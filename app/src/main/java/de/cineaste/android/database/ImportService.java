package de.cineaste.android.database;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.Movie;

public class ImportService {

	private static final Gson gson = new Gson();

	public static List<Movie> importMovies() {

		final File importFile = new File(
				Environment.getExternalStorageDirectory() + "/Cineaste/movies.json");
		try {
			FileReader fileReader = new FileReader(importFile);
			BufferedReader reader = new BufferedReader(fileReader);

			String temp;
			StringBuilder text = new StringBuilder();
			while ((temp = reader.readLine()) != null) {
				text.append(temp);
			}
			Type listType = new TypeToken<List<Movie>>() {
			}.getType();
			return gson.fromJson(text.toString(), listType);
		} catch (Exception ex) {
			return new ArrayList<>();
		}
	}
}
