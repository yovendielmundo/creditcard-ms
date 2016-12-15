package demo.rest.controller.hateoas;

import demo.domain.CreditCard;
import demo.domain.CreditCardService;
import demo.rest.assembler.CreditCardResourcesAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/hateoas", produces = MediaType.APPLICATION_JSON_VALUE)
public class HateoasController {

    @Autowired
    public HateoasController(final CreditCardService service) {
        this.service = service;
    }

    @RequestMapping(value = "/paged")
    ResponseEntity paged(final Pageable pageable, final PagedResourcesAssembler<CreditCard> assembler) {

        final Page<CreditCard> creditCards = service.findAll(pageable);
        return ResponseEntity.ok(assembler.toResource(creditCards, new CreditCardResourcesAssembler()));
    }

    @RequestMapping(value = "/search")
    ResponseEntity search(@RequestParam(value = "locale", required = false) final String locale, final CreditCardResourcesAssembler assembler) {

        final List<CreditCard> creditCards = service.findCreditCardsByLocale(locale);

        return ResponseEntity.ok(creditCards.stream()
                .map(assembler::toResource)
                .collect(Collectors.toList()));
    }

    private final CreditCardService service;
}
