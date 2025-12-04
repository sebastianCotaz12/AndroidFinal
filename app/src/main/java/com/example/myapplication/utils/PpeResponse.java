package com.example.myapplication.utils;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.example.myapplication.controller.DetectionItem;

import java.util.ArrayList;
import java.util.List;

public class PpeResponse {

    @SerializedName("ok")
    private boolean ok;

    @SerializedName("detected")
    private List<String> detected;

    @SerializedName("missing")
    private JsonElement missing;

    @SerializedName("missingRaw")
    private List<String> missingRaw;

    @SerializedName("detections")
    private List<DetectionItem> detections;  // CORREGIDO

    @SerializedName("model")
    private String model;

    @SerializedName("context")
    private String context;

    @SerializedName("annotatedImage")
    private String annotatedImage;

    @SerializedName("message")
    private String message;

    public PpeResponse() {
        this.detected = new ArrayList<>();
        this.missingRaw = new ArrayList<>();
        this.detections = new ArrayList<>();
    }

    // ============================
    //  missing como lista limpia
    // ============================
    public List<String> getMissing() {
        List<String> result = new ArrayList<>();

        if (missing == null || missing.isJsonNull()) {
            return result;
        }

        if (missing.isJsonArray()) {
            JsonArray array = missing.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    result.add(element.getAsString());
                } else if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();

                    if (obj.has("name"))
                        result.add(obj.get("name").getAsString());
                    else if (obj.has("class"))
                        result.add(obj.get("class").getAsString());
                    else if (obj.has("item"))
                        result.add(obj.get("item").getAsString());
                    else if (obj.has("label"))
                        result.add(obj.get("label").getAsString());
                }
            }
        } else if (missing.isJsonPrimitive()) {
            result.add(missing.getAsString());
        }

        return result;
    }

    public JsonElement getMissingElement() {
        return missing;
    }

    // ============================
    // GETTERS & SETTERS
    // ============================

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<String> getDetected() {
        return detected != null ? detected : new ArrayList<>();
    }

    public void setDetected(List<String> detected) {
        this.detected = detected != null ? detected : new ArrayList<>();
    }

    public void setMissing(JsonElement missing) {
        this.missing = missing;
    }

    public void setMissing(List<String> missingList) {
        if (missingList != null) {
            JsonArray array = new JsonArray();
            for (String item : missingList) {
                array.add(item);
            }
            this.missing = array;
        }
    }

    public List<String> getMissingRaw() {
        return missingRaw != null ? missingRaw : new ArrayList<>();
    }

    public void setMissingRaw(List<String> missingRaw) {
        this.missingRaw = missingRaw != null ? missingRaw : new ArrayList<>();
    }

    public List<DetectionItem> getDetections() {
        return detections != null ? detections : new ArrayList<>();
    }

    public void setDetections(List<DetectionItem> detections) {
        this.detections = detections != null ? detections : new ArrayList<>();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAnnotatedImage() {
        return annotatedImage != null ? annotatedImage : "";
    }

    public void setAnnotatedImage(String annotatedImage) {
        this.annotatedImage = annotatedImage;
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PpeResponse{" +
                "ok=" + ok +
                ", detected=" + detected +
                ", missing=" + missing +
                ", model='" + model + '\'' +
                ", context='" + context + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
