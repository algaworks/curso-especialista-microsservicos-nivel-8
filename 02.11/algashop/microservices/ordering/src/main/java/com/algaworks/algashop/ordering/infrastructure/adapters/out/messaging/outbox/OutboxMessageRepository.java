package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

	@Query("select m from OutboxMessage m order by m.id") //uuidv7
	List<OutboxMessage> findBatch(Pageable pageable);

	@Modifying
	@Query("delete from OutboxMessage m where m.id = :id")
	int deleteMessage(UUID id);


	@Modifying
	@Query("""
		update OutboxMessage m
	    set m.attempts = :attempts,
	       m.nextAttemptAt = :nextAttemptAt,
	       m.lastError = :lastError,
	       m.failedAt = :failedAt
	    where m.id = :id
	""")
	int registerFailure(
		@Param("id") UUID id,
		@Param("attempts") int attempts,
		@Param("nextAttemptAt") OffsetDateTime nextAttemptAt,
		@Param("lastError") String lastError,
		@Param("failedAt") OffsetDateTime failedAt
	);
}
