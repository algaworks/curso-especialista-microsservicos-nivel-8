package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessedMessageCleaner {

	private static final int BATCH_SIZE = 200;
	private static final int MAX_BATCHES = 20;
	private static final Duration RETENTION = Duration.ofDays(10);

	private final ProcessedMessageRepository repository;
	private final TransactionTemplate transactionTemplate;

	@Scheduled(cron = "0 */10 * * * *") //each 10 minutes
	@SchedulerLock(name = "processedMessageCleanup", lockAtMostFor = "PT9M")
	public void purgeExpired() {
		OffsetDateTime threshold = OffsetDateTime.now().minus(RETENTION);
		log.info("Processed message cleanup started, threshold={}", threshold);

		int totalDeleted = 0;
		for (int i = 0; i < MAX_BATCHES; i++) {
			Integer batchDeleted = transactionTemplate.execute(_ ->
					repository.deleteBatchOlderThan(threshold, BATCH_SIZE));
			totalDeleted += batchDeleted == null ? 0 : batchDeleted;

			if (batchDeleted == null || batchDeleted < BATCH_SIZE) {
				break;
			}
		}

		log.info("Processed message cleaup finished, deleted={}", totalDeleted);
	}

}
