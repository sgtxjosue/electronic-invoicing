package com.example.electronicinvoicing.repository;

import com.example.electronicinvoicing.domain.Party;
import com.example.electronicinvoicing.domain.PartyType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {

    List<Party> findByType(PartyType type);
}

