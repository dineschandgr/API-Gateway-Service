package sg.ntuchealth.yoda.edge.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sg.ntuchealth.yoda.edge.repo.model.B3Token;

@Repository
public interface B3TokenRepository extends CrudRepository<B3Token, String> {}
