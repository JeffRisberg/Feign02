package com.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspired by {@code com.example.retrofit.GitHubClient}
 */
public class Main {

    @Headers("Accept: application/json")
    interface CountryData {

        @JsonIgnoreProperties(ignoreUnknown = true)
        class CountryList {
            List<Country> country_fact_sheets = new ArrayList<Country>();
            String api_version;
            Integer page;
            Integer per_page;

            public List<Country> getCountry_fact_sheets() {
                return country_fact_sheets;
            }

            public void setCountry_fact_sheets(List<Country> country_fact_sheets) {
                this.country_fact_sheets = country_fact_sheets;
            }

            public String getApi_version() {
                return api_version;
            }

            public void setApi_version(String api_version) {
                this.api_version = api_version;
            }

            public Integer getPage() {
                return page;
            }

            public void setPage(Integer page) {
                this.page = page;
            }

            public Integer getPer_page() {
                return per_page;
            }

            public void setPer_page(Integer per_page) {
                this.per_page = per_page;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class Country {
            String title;
            List<String> terms;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<String> getTerms() {
                return terms;
            }

            public void setTerms(List<String> terms) {
                this.terms = terms;
            }
        }

        @RequestLine("GET /api/v1/?command=get_country_fact_sheets&fields=title,terms&terms=mexico:any,peru:any")
        CountryList countries();

        /**
         * Lists all countries
         */
        default CountryList allCountries() {
            return countries();
        }

        static CountryData connect() {
            ObjectMapper mapper = new ObjectMapper();

            return Feign.builder()
                    .decoder(new JacksonDecoder(mapper))
                    .logger(new Logger.ErrorLogger())
                    .logLevel(Logger.Level.BASIC)
                    .target(CountryData.class, "https://www.state.gov");
        }
    }

    public static void main(String... args) {

        try {
            CountryData countryData = CountryData.connect();
            CountryData.CountryList countryList = countryData.allCountries();

            for (CountryData.Country country : countryList.country_fact_sheets) {
                System.out.println(country.title + " " + country.terms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}