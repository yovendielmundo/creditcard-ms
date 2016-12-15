package demo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CreditCardRepository extends MongoRepository<CreditCard, String> {

    Page<CreditCard> findAll(Pageable pageable);

    @Query("{ 'language' : ?0 }")
    List<CreditCard> findByLocale(String locale);
}



