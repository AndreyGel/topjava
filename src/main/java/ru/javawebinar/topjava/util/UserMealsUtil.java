package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(15, 0), 1300);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(15, 0), 1300));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        var totalCalories = new HashMap<LocalDate, Integer>();
        var filteredMeals = new ArrayList<UserMeal>();

        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredMeals.add(meal);
                var localDate = meal.getDateTime().toLocalDate();
                if (totalCalories.containsKey(localDate)) {
                    totalCalories.put(localDate, meal.getCalories() + totalCalories.get(localDate));
                } else {
                    totalCalories.put(localDate, meal.getCalories());
                }
            }
        }

        var filteredMealsWithExcess = new ArrayList<UserMealWithExcess>();
        for (UserMeal meal : filteredMeals) {
            var excess = totalCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
            filteredMealsWithExcess.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
        }

        return filteredMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        var totalCalories = new HashMap<LocalDate, Integer>();
        meals.stream().filter(
                meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)
        ).forEach(
                meal -> {
                    var localDate = meal.getDateTime().toLocalDate();
                    if (totalCalories.containsKey(localDate)) {
                        totalCalories.put(localDate, meal.getCalories() + totalCalories.get(localDate));
                    } else {
                        totalCalories.put(localDate, meal.getCalories());
                    }
                }
        );

        return meals.stream().map(meal -> {
            var excess = totalCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
            return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
        }).collect(Collectors.toList());
    }
}
