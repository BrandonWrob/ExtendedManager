package edu.ncsu.csc326.wolfcafe;

import com.google.gson.Gson;

/**
 * Class for handy utils shared across all of the API tests
 *
 * @author Kai Presler-Marshall
 *
 */
public class TestUtils {

    /** instance of Gson used to convert to json */
    private static Gson gson = new Gson();

    /**
     * Uses Google's GSON parser to serialize a Java object to JSON. Useful for
     * creating JSON representations of our objects when calling API methods.
     *
     * @param obj
     *            to serialize to JSON
     * @return JSON string associated with object
     */
    public static String asJsonString ( final Object obj ) {
        return gson.toJson( obj );
    }

}
