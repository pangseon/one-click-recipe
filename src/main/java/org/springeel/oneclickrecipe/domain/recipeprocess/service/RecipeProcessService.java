package org.springeel.oneclickrecipe.domain.recipeprocess.service;

import java.io.IOException;
import java.util.List;
import org.springeel.oneclickrecipe.domain.recipeprocess.dto.service.RecipeProcessCreateServiceRequestDto;
import org.springeel.oneclickrecipe.domain.recipeprocess.dto.service.RecipeProcessReadServiceResponseDto;
import org.springeel.oneclickrecipe.domain.recipeprocess.dto.service.RecipeProcessUpdateServiceRequestDto;
import org.springeel.oneclickrecipe.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeProcessService {

    void createRecipeProcess(RecipeProcessCreateServiceRequestDto requestDto, User user,
        Long recipeId, MultipartFile multipartFile) throws IOException;

    void deleteRecipeProcess(Long recipeId, User user, Long processId);

    void updateRecipeProcess(RecipeProcessUpdateServiceRequestDto requestDto, Long recipeId,
        User user, Long processId, MultipartFile multipartFile) throws IOException;

    List<RecipeProcessReadServiceResponseDto> readAllRecipeProcess(Long recipeId);

}
