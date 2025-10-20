package com.cafe.order.adapter.in.web.member;

import com.cafe.order.adapter.in.web.common.ApiResponse;
import com.cafe.order.adapter.in.web.member.mapper.MemberWebMapper;
import com.cafe.order.adapter.in.web.member.request.MemberCancelWithdrawalRequest;
import com.cafe.order.adapter.in.web.member.request.MemberSignupRequest;
import com.cafe.order.adapter.in.web.member.request.MemberWithdrawRequest;
import com.cafe.order.adapter.in.web.member.response.MemberSignupResponse;
import com.cafe.order.application.port.in.member.MemberUseCase;
import com.cafe.order.application.port.in.member.command.MemberCancelWithdrawalCommand;
import com.cafe.order.application.port.in.member.command.MemberSignupCommand;
import com.cafe.order.application.port.in.member.command.MemberWithdrawCommand;
import com.cafe.order.domain.member.model.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberUseCase memberUseCase;
    private final MemberWebMapper memberWebMapper;

    @PostMapping("/signup")
    public ApiResponse<MemberSignupResponse> signup(@Valid @RequestBody MemberSignupRequest request) {
        MemberSignupCommand command = memberWebMapper.toCommand(request);
        Member member = memberUseCase.signup(command);
        MemberSignupResponse response = memberWebMapper.toResponse(member);

        return ApiResponse.success(response);
    }

    @PatchMapping("/withdraw")
    public ApiResponse<Void> withdraw(@Valid @RequestBody MemberWithdrawRequest request) {
        MemberWithdrawCommand command = memberWebMapper.toWithdrawCommand(request);
        memberUseCase.withdraw(command);
        return ApiResponse.success();
    }

    @PatchMapping("/cancel-withdrawal")
    public ApiResponse<Void> cancelWithdrawal(@Valid @RequestBody MemberCancelWithdrawalRequest request) {
        MemberCancelWithdrawalCommand command = memberWebMapper.toCancelWithdrawalCommand(request);
        memberUseCase.cancelWithdrawal(command);
        return ApiResponse.success();
    }
}