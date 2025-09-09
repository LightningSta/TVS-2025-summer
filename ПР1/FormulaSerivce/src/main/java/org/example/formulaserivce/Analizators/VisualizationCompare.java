package org.example.formulaserivce.Analizators;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class VisualizationCompare {

    @Value("${GateWayUrl}")
    private String GATEWAY;

    private List<String> getFormulas(String login,String token){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", login);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(2),headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                GATEWAY+"/api/db/formulas/not",
                HttpMethod.POST,
                entity,
                String.class
        );
        System.out.println(responseEntity);
        List<String> formulas = new ArrayList<>();
        JSONObject json = new JSONObject(responseEntity.getBody());
        System.out.println(json.toString(2));
        for (int i = 0; i < json.getJSONArray("formulas").length(); i++) {
            JSONObject formula = json.getJSONArray("formulas").getJSONObject(i).getJSONObject("formula");
            if(formula.get("latex") instanceof JSONArray ){
                formulas.add(formula.getJSONArray("latex").getString(0));
            }else{
                formulas.add(formula.getString("latex"));
            }
        }
        return formulas;
    }

    private char[] getAlphabet(String latex) {
        return latex.toCharArray();
    }

    private int count(String s,char symb){
        char[] chars = s.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == symb){
                count++;
            }
        }
        return count;
    }

    public String analyze(String latex, String login,String token) throws FileNotFoundException {
        List<String> formulas= getFormulas(login,token);
        if(formulas.isEmpty()){
            return new JSONObject().append("similarity","Данных нету").append("part","").toString();
        }
        String normalizedLatex1 = normalizeLatex(latex);
        List<String> sim = new ArrayList<>();
        JSONObject jsonObject = null;
        for (int i = 0; i < formulas.size(); i++) {
            String normalizedLatex2 = normalizeLatex(formulas.get(i));

            double similarity = Math.round( calculateSimilarity(normalizedLatex1, normalizedLatex2));
            JSONObject simJson = new JSONObject();
            simJson.put("similarity", similarity+"%");
            String part = findMatchingSubstring(normalizedLatex1, normalizedLatex2);
            if(part.endsWith("{")){
                part=part.substring(0,part.length()-1);
            }
            if(!part.endsWith("}")){
                if((count(part,'{')+count(part,'}'))%2!=0){
                    part = part + "}";
                }
            }
            simJson.put("part", part);
            if(jsonObject!=null){
                jsonObject = Math.max(Double.parseDouble(jsonObject.getString("similarity").replace("%","")), similarity)== similarity ? simJson : jsonObject;
            }else{
                jsonObject = simJson;
            }
            sim.add(simJson.toString());
        }
        System.out.println(jsonObject);
        return jsonObject.toString(2);
    }

    public static String normalizeLatex(String latex) {
        return latex.replaceAll("\\s+", "");
    }

    public static double calculateSimilarity(String str1, String str2) {
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 100.0;

        int distance = levenshteinDistance(str1, str2);
        return (1 - (double) distance / maxLength) * 100;
    }
    public static String findMatchingSubstring(String str1, String str2) {
        int maxLength = 0;
        int start = 0;

        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLength) {
                        maxLength = dp[i][j];
                        start = i - maxLength;
                    }
                }
            }
        }

        return str1.substring(start, start + maxLength);
    }
    public static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }
        return dp[str1.length()][str2.length()];
    }
}
