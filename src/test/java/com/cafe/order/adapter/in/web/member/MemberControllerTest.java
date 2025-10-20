package com.cafe.order.adapter.in.web.member;

import com.cafe.order.adapter.in.web.member.mapper.MemberWebMapper;
import com.cafe.order.adapter.in.web.member.request.MemberSignupRequest;
import com.cafe.order.adapter.in.web.member.response.MemberSignupResponse;
import com.cafe.order.application.port.in.member.MemberUseCase;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.domain.member.model.Member;
import com.cafe.order.domain.member.model.enums.Gender;
import com.cafe.order.domain.member.model.enums.MemberStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberUseCase memberUseCase;

    @MockBean
    private MemberWebMapper memberWebMapper;

    @Test
    void signup() throws Exception {
        // given
        String name = "test";
        String phoneNumber = "01012345678";
        Gender gender = Gender.MALE;
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        LocalDateTime joinDateTime = LocalDateTime.now();

        MemberSignupRequest request = new MemberSignupRequest(name, phoneNumber, gender, birthDate);

        MemberSignupCommand command = new MemberSignupCommand(name, phoneNumber, gender, birthDate);

        Member member = Member.builder()
                .id(1L) // Corrected from memberId(1L)
                .name(name)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .birthDate(birthDate)
                .status(MemberStatus.ACTIVE)
                .joinDateTime(joinDateTime)
                .build();

        MemberSignupResponse response = new MemberSignupResponse(
                member.getId(), // Corrected from member.getMemberId()
                member.getName(),
                member.getPhoneNumber(),
                member.getGender(),
                member.getBirthDate(),
                member.getStatus(),
                member.getJoinDateTime()
        );

        given(memberWebMapper.toCommand(any(MemberSignupRequest.class)))
                .willReturn(command);

        given(memberUseCase.signup(any(MemberSignupCommand.class)))
                .willReturn(member);

        given(memberWebMapper.toResponse(any(Member.class)))
                .willReturn(response);


        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.data.gender").value(gender.name()))
                .andExpect(jsonPath("$.data.birthDate").value(birthDate.toString()));
    }
}
