package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String API_URL = "https://dog.ceo/api";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "success";

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        OkHttpClient client = (new OkHttpClient()).newBuilder().build();

        Request request = (new Request.Builder())
                .url(String.format("%s/breed/%s/list", API_URL, breed.toLowerCase()))
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());

            if (json.getString(STATUS).equals(SUCCESS)) {
                JSONArray subBreedsArray = json.getJSONArray(MESSAGE);
                List<String> subBreeds = new ArrayList<>();

                for (int i = 0; i < subBreedsArray.length(); i++) {
                    subBreeds.add(subBreedsArray.getString(i));
                }

                return subBreeds;
            }
            else {
                throw new BreedNotFoundException(json.getString(MESSAGE));
            }
        } catch (JSONException | IOException e) {
            throw new BreedNotFoundException(e.getMessage());
        }
    }
}