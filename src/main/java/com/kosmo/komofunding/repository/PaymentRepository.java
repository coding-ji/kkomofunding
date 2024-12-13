package com.kosmo.komofunding.repository;

import com.kosmo.komofunding.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByProjectId(String projectId);

    Boolean existsByPaymentNum(Long paymentNum);

}
