package com.forestfull.router.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.repository.ClientHistoryRepository;
import com.forestfull.router.util.SchedulerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SupportService {

    @Value("${management.address}")
    private String managerAddress;

    private final JavaMailSender javaMailSender;
    private final SchedulerManager schedulerManager;
    private final ClientHistoryRepository clientHistoryRepository;

    public String getSupportComponent() {
        if (ObjectUtils.isEmpty(SchedulerManager.componentMap))
            schedulerManager.setComponentMap();

        final String solution = SchedulerManager.componentMap.get("management").getContents();
        return StringUtils.hasText(solution) ? solution : null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Void> requestForSolutionSupport(String token, String solution, String ipAddress, NetworkVO.Request request) {
        try {
            javaMailSender.send(m -> {
                final MimeMessageHelper helper = new MimeMessageHelper(m, true);
                helper.setSubject("[Request for solution support] " + solution);
                helper.setTo(managerAddress);
                helper.setText(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
            });

            return clientHistoryRepository.saveHistoryByTokenAndSolution(token, solution, ipAddress, new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}