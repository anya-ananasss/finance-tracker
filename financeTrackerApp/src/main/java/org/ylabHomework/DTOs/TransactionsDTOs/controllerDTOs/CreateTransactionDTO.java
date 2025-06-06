package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, передающий поля транзакции.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@JsonPropertyOrder({"type", "sum", "category", "description"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    @Min(value = 1, message = "Тип должен быть 1 (доход) или 2 (расход)!")
    @Max(value = 2, message = "Тип должен быть 1 (доход) или 2 (расход)!")
    private int type;

    @NotNull(message = "Сумма не должна быть пустой!")
    @Positive(message = "Сумма должна быть больше 0!")
    @Digits(integer = 15, fraction = 2, message = "Сумма должна содержать не более 15 цифр до запятой и 2 после!")
    private double sum;

    @NotBlank(message = "Категория не должна быть пустой или состоять только из пробелов!")
    @Size(min = 2, max = 50, message = "Категория должна содержать от 2 до 50 символов!")
    private String category;

    @Size(max = 200, message = "Описание не должно превышать 200 символов!")
    private String description;
}