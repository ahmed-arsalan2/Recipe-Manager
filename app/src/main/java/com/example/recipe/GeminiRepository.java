package com.example.recipe;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiRepository {

    private final GenerativeModelFutures model;
    private final ExecutorService executor;

    public GeminiRepository() {

        GenerativeModel ai =
                FirebaseAI.getInstance(
                        GenerativeBackend.googleAI()
                ).generativeModel("gemini-3.5-flash");

        model =
                GenerativeModelFutures.from(ai);

        executor =
                Executors.newSingleThreadExecutor();
    }

    public void generateRecipeDescription(
            Recipe recipe,
            GeminiCallback callback
    ) {

        String prompt =
                "Write a concise and appetizing description "
                        + "for the following recipe.\n\n"
                        + "Recipe title: "
                        + recipe.getTitle()
                        + "\n"
                        + "Category: "
                        + recipe.getCategory()
                        + "\n"
                        + "Ingredients: "
                        + recipe.getIngredients()
                        + "\n"
                        + "Instructions: "
                        + recipe.getInstructions()
                        + "\n\n"
                        + "Requirements:\n"
                        + "- Write 2 to 4 sentences.\n"
                        + "- Make it natural and appetizing.\n"
                        + "- Do not invent ingredients.\n"
                        + "- Do not use emojis.\n"
                        + "- Return only the description.";

        Content promptContent =
                new Content.Builder()
                        .addText(prompt)
                        .build();

        ListenableFuture<GenerateContentResponse> response =
                model.generateContent(promptContent);

        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {

                    @Override
                    public void onSuccess(
                            GenerateContentResponse result
                    ) {

                        String description =
                                result.getText();

                        if (description == null ||
                                description.trim().isEmpty()) {

                            callback.onError(
                                    new Exception(
                                            "Gemini returned an empty response"
                                    )
                            );

                            return;
                        }

                        callback.onSuccess(
                                description.trim()
                        );
                    }

                    @Override
                    public void onFailure(
                            Throwable throwable
                    ) {

                        callback.onError(throwable);
                    }
                },
                executor
        );
    }

    public void shutdown() {
        executor.shutdown();
    }

    public interface GeminiCallback {

        void onSuccess(String description);

        void onError(Throwable error);
    }
}