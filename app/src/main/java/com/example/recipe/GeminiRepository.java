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
                "You are a professional food editor creating a clear and "
                        + "appetizing recipe presentation.\n\n"

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

                        + "Create the recipe presentation using exactly this structure:\n\n"

                        + "1. Description\n"
                        + "Write 2 to 3 lines describing the recipe and what makes "
                        + "it appealing. Keep it natural and appetizing.\n\n"

                        + "2. Ingredients\n"
                        + "List the ingredients clearly using bullet points. "
                        + "Use only the ingredients provided above. "
                        + "Do not invent or add ingredients.\n\n"

                        + "3. Instructions\n"
                        + "Rewrite the provided instructions as clear, numbered "
                        + "steps for preparing the recipe. "
                        + "Do not invent additional cooking steps.\n\n"

                        + "Rules:\n"
                        + "- Keep the recipe title unchanged.\n"
                        + "- Do not invent ingredients, quantities, cooking methods, "
                        + "or preparation steps.\n"
                        + "- Preserve the meaning of the provided instructions.\n"
                        + "- Do not include nutritional claims.\n"
                        + "- Do not use emojis.\n"
                        + "- Return only the recipe presentation.";

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