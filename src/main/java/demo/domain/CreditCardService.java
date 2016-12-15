package demo.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardService {

    @Autowired
    public CreditCardService(final CreditCardRepository creditCardRepository) {
        repository = creditCardRepository;
    }

    public List<CreditCard> createCreditCardsWithOutEmptyValues(final List<CreditCard> creditCards) {
        return creditCards.parallelStream()
                .map(CreditCard::new)
                .filter(CreditCard::isNotEmpty)
                .collect(Collectors.toList());
    }

    public List<CreditCard> importNormalizedCreditCards(final List<CreditCard> creditCards) {
        deleteAll();
        return saveNormalizedCreditCards(creditCards);
    }

    public CreditCard saveNormalizedCreditCard(final CreditCard creditCard) {
        final CreditCard normalizedCreditCard = new CreditCard(creditCard);
        return save(normalizedCreditCard);
    }

    public List<CreditCard> saveNormalizedCreditCards(final List<CreditCard> creditCards) {
        final List<CreditCard> normalizedCreditCards = createCreditCardsWithOutEmptyValues(creditCards);
        return save(normalizedCreditCards);
    }

    @Transactional
    public CreditCard save(final CreditCard creditCard) {
        return repository.save(creditCard);
    }

    @Transactional
    public List<CreditCard> save(final List<CreditCard> creditCards) {
        return repository.save(creditCards);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public Page<CreditCard> findAll(final Pageable pageRequest) {
        return repository.findAll(pageRequest);
    }

    public CreditCard getCreditCardById(final String id) {
        final CreditCard creditCard = repository.findOne(id);
        if (creditCard == null) {
            throw new CreditCardNotFoundException();
        }
        return creditCard;
    }

    public void delete(final String id) {
        repository.delete(id);
    }

    public List<CreditCard> findCreditCardsByLocale(final String locale) {
        return repository.findByLocale(locale);
    }

    private final CreditCardRepository repository;
}
