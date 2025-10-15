package fatecipi.progweb.mymanga.repositories;

import fatecipi.progweb.mymanga.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
