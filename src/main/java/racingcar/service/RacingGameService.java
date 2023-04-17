package racingcar.service;

import org.springframework.stereotype.Service;
import racingcar.dao.CarDao;
import racingcar.dao.RacingGameDao;
import racingcar.domain.Car;
import racingcar.domain.Cars;
import racingcar.domain.MoveStrategy;
import racingcar.domain.RacingGame;
import racingcar.dto.CarDto;
import racingcar.dto.RacingGameRequest;
import racingcar.dto.RacingGameResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RacingGameService {
    private final CarDao carDao;
    private final RacingGameDao racingGameDao;
    private final MoveStrategy moveStrategy;

    public RacingGameService(CarDao carDao, RacingGameDao racingGameDao, MoveStrategy moveStrategy) {
        this.carDao = carDao;
        this.racingGameDao = racingGameDao;
        this.moveStrategy = moveStrategy;
    }

    public RacingGameResponse play(RacingGameRequest racingGameRequest) {
        RacingGame game = playGame(racingGameRequest);

        RacingGameResponse result = getResult(game);
        saveGame(racingGameRequest, result);
        return result;
    }

    private RacingGame playGame(RacingGameRequest racingGameRequest) {
        RacingGame game = initializeGame(racingGameRequest);
        while (!game.isEnd()) {
            game.playOneRound();
        }
        return game;
    }

    private RacingGame initializeGame(RacingGameRequest racingGameRequest) {
        Cars cars = new Cars(racingGameRequest.getNamesList().stream()
                .map(Car::new)
                .collect(Collectors.toList()));
        return new RacingGame(moveStrategy, racingGameRequest.getCount(), cars);
    }

    private void saveGame(RacingGameRequest racingGameRequest, RacingGameResponse result) {
        Long racingGameId = racingGameDao.save(result.getWinners(), racingGameRequest.getCount());
        carDao.saveAll(racingGameId, result.getRacingCars());
    }

    private RacingGameResponse getResult(RacingGame racingGame) {
        Cars cars = racingGame.getCars();
        List<Car> winners = cars.findWinners();
        List<CarDto> racingCars = cars.getCars().stream()
                .map(CarDto::from)
                .collect(Collectors.toList());
        return RacingGameResponse.of(winners, racingCars);
    }

    public List<RacingGameResponse> getHistory() {
        return racingGameDao.loadHistories(carDao);
    }
}
