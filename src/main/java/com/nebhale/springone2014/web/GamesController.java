/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebhale.springone2014.web;

import com.nebhale.springone2014.model.*;
import com.nebhale.springone2014.repository.GameDoesNotExistException;
import com.nebhale.springone2014.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping(value = "/games")
final class GamesController {

    private final DoorsResourceAssembler doorsResourceAssembler;

    private final GameRepository gameRepository;

    private final GameResourceAssembler gameResourceAssembler;

    @Autowired
    GamesController(DoorsResourceAssembler doorsResourceAssembler, GameRepository gameRepository,
                    GameResourceAssembler gameResourceAssembler) {
        this.doorsResourceAssembler = doorsResourceAssembler;
        this.gameRepository = gameRepository;
        this.gameResourceAssembler = gameResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseStatus(HttpStatus.CREATED)
    Game createGame() {
        Game game = this.gameRepository.create();

        return game;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    Game showGame(@PathVariable Long gameId) throws GameDoesNotExistException {
        Game game = this.gameRepository.retrieve(gameId);
        return game;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    void destroyGame(@PathVariable Long gameId) throws GameDoesNotExistException {
        this.gameRepository.remove(gameId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{gameId}/doors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    Collection<Door> showDoors(@PathVariable Long gameId) throws GameDoesNotExistException {
        Game game = this.gameRepository.retrieve(gameId);
        return game.getDoors();
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/{gameId}/doors/{doorId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    void transitionDoor(@PathVariable Long gameId, @PathVariable Long doorId,
                        @RequestBody Map<String, String> payload)
            throws GameDoesNotExistException, IllegalTransitionException, DoorDoesNotExistException {

        DoorStatus status = DoorStatus.parse(payload);
        this.gameRepository.retrieve(gameId).transition(doorId, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }

}
