package de.cineaste.android.database;

import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.cineaste.android.entity.Movie;

public class ExportService {

	private static final Gson gson = new Gson();

	public static void exportMovies(List<Movie> movies) {

		File exportPath = new File(Environment.getExternalStorageDirectory() + "/Cineaste");

		if (!exportPath.exists()) {
			if (!exportPath.mkdir())
				return;
		}

		File exportFile = new File(
				Environment.getExternalStorageDirectory() + "/Cineaste/movies.json");

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(exportFile);
			fileWriter.write(gson.toJson(movies));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileWriter != null)
					fileWriter.close();
			} catch (Exception e) {
				//die silently
			}
		}
	}
}
