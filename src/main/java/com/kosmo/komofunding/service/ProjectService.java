package com.kosmo.komofunding.service;

import com.kosmo.komofunding.common.enums.ProjectCategory;
import com.kosmo.komofunding.converter.ProjectConverter;
import com.kosmo.komofunding.dto.ProjectInDTO;
import com.kosmo.komofunding.dto.ProjectOutDTO;
import com.kosmo.komofunding.entity.Project;
import com.kosmo.komofunding.repository.ProjectRepository;
import com.kosmo.komofunding.repository.QnARepository;
import com.kosmo.komofunding.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectConverter projectConverter;
    private final UserRepository userRepository;
    private final QnARepository qnARepository;

    // 전체 프로젝트 조회
    public List<ProjectOutDTO> getAllProjects() {
        try {
            List<Project> allProjects = projectRepository.findAll();
            LocalDateTime now = LocalDateTime.now();
            return allProjects.stream()
                    .map(project -> {
                       return projectConverter.toOutDTO(project);
                    })
                    .filter(projectOutDTO -> {
                       return projectOutDTO.getProjectEndDate().isAfter(now);
                    })
                    .sorted(Comparator.comparingDouble(ProjectOutDTO::getProgressRate).reversed())
                    .limit(50)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error while fetching projects: " + e.getMessage());
            e.printStackTrace();
            throw e; // 예외를 다시 던져 컨트롤러에서 처리
        }
    }
    // 카테고리 및 상태별 프로젝트 조회
    public List<ProjectOutDTO> getProjectsByCategoryAndStatus(ProjectCategory category, String fundingStatus) {
        try {
            List<Project> projects;
            // fundingStatus가 HOME이면 전체 프로젝트
            if(fundingStatus.equals("HOME")){
                projects = projectRepository.findAllByCategory(category);
            }
            // fundingStatus가 ONGOING / UPCOMING이면 조건에 맞게 가져옴
            else {
                LocalDateTime now = LocalDateTime.now();
                projects = projectRepository.findByProjectsCategoryAndFundingStatusAndDateRange(
                        category, fundingStatus, now);
            }

            // 프로젝트 목록을 DTO로 변환하여 반환
            return projects.stream()
                    .map(project -> projectConverter.toOutDTO(project))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("프로젝트 조회 중 오류가 발생했습니다.", e);
        }
    }

    // 유저 uid로 프로젝트 조회
    public List<Project> getProjectsByUserId(String userId){
        return projectRepository.findByUserId(userId);
    }

    // 프로젝트 저장 로직 (projectInDTO를 Entity로 저장)
    public Project createProject(ProjectInDTO projectInDTO) {
        Project project = toEntity(projectInDTO);
        return projectRepository.save(project);
    }

    // 특정 프로젝트 조회
    public ProjectOutDTO getProjectDetails(Long projectNum){
        Project project = projectRepository.findByProjectNum(projectNum)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        return projectConverter.toOutDTO(project);
    }

    // DTO를 엔티티로 변환하는 메서드
    public Project toEntity(ProjectInDTO projectInDTO) {
        // 현재시간 변수
        LocalDateTime now = LocalDateTime.now();

        Project project = Project.builder()
                .userId(projectInDTO.getUserId())
                .title(projectInDTO.getTitle())
                .projectCategory(projectInDTO.getProjectCategory())
                .thumbnailImgs(projectInDTO.getThumnailImgs())
                .shortDescription(projectInDTO.getShortDescription())
                .description(projectInDTO.getDescription())
                .items(projectInDTO.getItems())
                .totalAmount(projectInDTO.getTotalAmount())
                .projectStartDate(projectInDTO.getProjectStartDate())
                .projectEndDate(projectInDTO.getProjectEndDate())
                .writtenDate(now)
                .updatedDate(now)
                .statusChangeReason("")  // 기본값 빈 문자열
                .build();

        project.setProjectNum(generateProjectNum());
        return project;
    }

    // 6자리 랜덤 숫자 생성
    private Long generateRandomProjectNum() {
        return (long) (100000 + Math.random() * 900000);  // 100000~999999 범위
    }

    // 프로젝트 번호 생성 로직 (6자리 중복 방지)
    private Long generateProjectNum() {
        Long projectNum = generateRandomProjectNum();
        int attempts = 0;
        while (projectRepository.existsByProjectNum(projectNum)) {
            projectNum = generateRandomProjectNum();
            attempts++;
            // 너무 많은 반복이 발생하면 예외를 던짐
            if (attempts > 100) {
                throw new IllegalStateException("프로젝트 번호 생성 실패");
            }
        }
        return projectNum;
    }

}