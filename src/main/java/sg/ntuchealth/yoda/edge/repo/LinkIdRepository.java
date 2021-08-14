package sg.ntuchealth.yoda.edge.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sg.ntuchealth.yoda.edge.repo.model.LinkIdToken;

@Repository
public interface LinkIdRepository extends CrudRepository<LinkIdToken, String> {}
