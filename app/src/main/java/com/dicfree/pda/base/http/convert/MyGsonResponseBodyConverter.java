/**
 * Copyright 2024 Wuhan Haici Technology Co., Ltd 
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dicfree.pda.base.http.convert;

import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_KEY;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_SUCCESS;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_DATA_KEY;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_MESSAGE_SUCCESS_KEY;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_MESSAGE_SUCCESS_VALUE;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String response = value.string();
            JSONObject jsonObject =null;
            try {
                jsonObject = new JSONObject(response);
                jsonObject.put(STATUS_CODE_KEY,STATUS_CODE_SUCCESS);
                if(!jsonObject.has(STATUS_MESSAGE_SUCCESS_KEY)){
                    jsonObject.put(STATUS_MESSAGE_SUCCESS_KEY,STATUS_MESSAGE_SUCCESS_VALUE);
                }
                if(!jsonObject.has(STATUS_DATA_KEY)){
                    jsonObject.put(STATUS_DATA_KEY,STATUS_MESSAGE_SUCCESS_VALUE);
                }
            } catch (JSONException mE) {
                mE.printStackTrace();
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            JsonReader jsonReader = null;
            if(null!=jsonObject){
                MediaType mediaType = value.contentType();
                Charset charset = mediaType != null ? mediaType.charset(UTF_8) : UTF_8;
                InputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
                jsonReader = gson.newJsonReader(new InputStreamReader(inputStream, charset));
            }else{
                jsonReader = gson.newJsonReader(value.charStream());
            }
            T result = adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            return result;
        } finally {
            value.close();
        }
    }
}
