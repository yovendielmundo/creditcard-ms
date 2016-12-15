package demo.rest.assembler;

import demo.domain.CreditCard;
import demo.rest.controller.CreditCardController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class CreditCardResourcesAssembler implements ResourceAssembler<CreditCard, Resource> {

    @Override
    public Resource toResource(final CreditCard creditCard) {
        final Resource<CreditCard> creditCardResource = new Resource<>(creditCard);
        creditCardResource.add(linkTo(CreditCardController.class).slash(creditCard.getStringId()).withSelfRel());
        return creditCardResource;
    }
}
