package racingcar.dto;

import racingcar.domain.Car;
import racingcar.domain.Cars;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RacingGameResponse {

    private final String winners;
    private final List<CarDto> racingCars;

    private RacingGameResponse(String winners, List<CarDto> racingCars) {
        this.winners = winners;
        this.racingCars = racingCars;
    }

    public static RacingGameResponse of(String winners, List<CarDto> racingCars) {
        return new RacingGameResponse(winners, racingCars);
    }

    public static RacingGameResponse of(List<Car> winners, Cars cars) {
        List<CarDto> carDtos = cars.getCars().stream()
                .map(CarDto::from)
                .collect(toList());
        return RacingGameResponse.of(winners, carDtos);
    }

    public static RacingGameResponse of(List<Car> winners, List<CarDto> racingCars) {
        return new RacingGameResponse(getWinnersName(winners), racingCars);
    }

    private static String getWinnersName(List<Car> winners) {
        return winners.stream()
                .map(Car::getName)
                .collect(joining(","));

    }

    public String getWinners() {
        return winners;
    }

    public List<CarDto> getRacingCars() {
        return racingCars;
    }
}
