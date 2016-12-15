package demo.rest.controller;

import demo.domain.CreditCard;
import demo.domain.CreditCardNotFoundException;
import demo.domain.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/credit-cards", produces = MediaType.APPLICATION_JSON_VALUE)
public class CreditCardController {

    @Autowired
    public CreditCardController(final CreditCardService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity createCreditCard(@RequestBody final CreditCard creditCard) {

        validate(creditCard);

        final CreditCard savedCreditCard = service.saveNormalizedCreditCard(creditCard);

        final Link link = getLink(savedCreditCard);

        final URI location = URI.create(link.getHref());

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity createCreditCards(@RequestBody final List<CreditCard> creditCards) {

        validate(creditCards);

        final List<CreditCard> importedCreditCards = service.importNormalizedCreditCards(creditCards);

        final List<Link> links = importedCreditCards.stream()
                .map(this::getLink)
                .collect(Collectors.toList());

        return ResponseEntity.ok(links);
    }

    @RequestMapping
    Page<CreditCard> getCreditCards(final Pageable pageable) {

        final Page<CreditCard> creditCards = service.findAll(pageable);

        return creditCards;
    }

    @RequestMapping(value = "/{id}")
    CreditCard getCreditCard(@PathVariable final String id) {

        final CreditCard creditCard = service.getCreditCardById(id);

        return creditCard;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCreditCard(@PathVariable final String id) {
        service.delete(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map handleIllegalArgumentException(final IllegalArgumentException e) {
        return Collections.singletonMap("message", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map handleCreditCardNotFoundException(final CreditCardNotFoundException e) {
        return Collections.singletonMap("message", e.getMessage());
    }

    private Link getLink(final CreditCard creditCard) {
        return linkTo(CreditCardController.class)
                .slash(creditCard.getId())
                .withSelfRel();
    }

    private void validate(final CreditCard creditCard) {
        if (creditCard.isEmpty()) {
            throw new IllegalArgumentException("the credit card is empty");
        }
    }

    private void validate(final List<CreditCard> creditCards) {
        if (creditCards.isEmpty()) {
            throw new IllegalArgumentException("the list of credit cards is empty");
        } else {
            if (creditCards.parallelStream().allMatch(CreditCard::isEmpty)) {
                throw new IllegalArgumentException("the list of credit cards has all credit cards empty");
            }
        }
    }

    private final CreditCardService service;

}
