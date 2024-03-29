package com.guilherme.json_diff.parsers;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.guilherme.json_diff.dto.DifferenceResultDTO;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonProcessor implements Processor {

    @Override
    public DifferenceResultDTO getDifference(String file1, String file2) {
        return new helper(file1, file2).getDifference();
    }

    private class helper {

        private final String json1;
        private final String json2;

        public helper(String json1, String json2) {
            this.json1 = json1;
            this.json2 = json2;
        }

        public DifferenceResultDTO getDifference() {

            DifferenceResultDTO.DifferenceResultDTOBuilder builder = DifferenceResultDTO.builder();

            Map<String, Object> left = convert(this.json1);
            Map<String, Object> right = convert(this.json2);

            MapDifference<String, Object> diff = diff(left, right);

            boolean areEqual = diff.areEqual();

            builder.equal(areEqual);

            // I'm not sure about the definition of a size, I'm considering the size in bytes of an string
            boolean equalSize = getStringSizeInBytes(json1) == getStringSizeInBytes(json2);

            builder.equalSize(equalSize);

            if (!areEqual) {
                builder.result(diff);
            }

            return builder.build();
        }

        /**
         * Get the size in byte of an string
         */
        private int getStringSizeInBytes(String file) {
            return file.getBytes().length;
        }

        /**
         * Convert a string into parsed json map
         *
         * @param json json text to be parsed
         * @return A Map that represents json values
         */
        private Map<String, Object> convert(String json) {
            Gson g = new Gson();
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            return g.fromJson(json, mapType);
        }


        /**
         * Generate a immutable result of changes in two maps.
         * GUAVA
         */
        private MapDifference<String, Object> diff(Map<String, Object> left, Map<String, Object> right) {

            return Maps.difference(left, right);
        }
    }
}
