package com.cpplab.global.common.code.status;

import com.cpplab.global.common.code.BaseErrorCode;
import com.cpplab.global.common.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // User 에러
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER400", "사용자가 존재하지 않습니다."),
    _LOGIN_USER_INVALID(HttpStatus.BAD_REQUEST, "USER401", "로그인 중 오류가 발생하였습니다."),
    _INVALID_USER(HttpStatus.BAD_REQUEST, "USER401" , "아이디 또는 비밀번호가 틀렸습니다."),
    _UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "USER401", "본인이 등록한 제품만 수정할 수 있습니다."),

    // community 에러
    _NOT_FOUND_POST(HttpStatus.NOT_FOUND, "POST400", "게시물이 존재하지 않습니다."),

    // comment 에러
    _NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "COMMENT400", "댓글이 존재하지 않습니다."),

    // portfolio 에러
    _NOT_FOUND_PORTFOLIO(HttpStatus.NOT_FOUND, "PORTFOLIO400", "포트폴리오가 존재하지 않습니다."),

    // Product 에러
    _NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "USER400", "제품이 존재하지 않습니다."),
    _NOT_FOUND_PRODUCT_IN_CART(HttpStatus.NOT_FOUND, "USER400", "제품이 장바구니에 존재하지 않습니다."),
    _PRODUCT_IN_CART_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "USER400", "장바구니에 담겨있는 상품은 삭제할 수 없습니다."),

    // Cart 에러
    _CART_IS_ALREADY_EMPTY(HttpStatus.BAD_REQUEST, "USER400", "장바구니가 이미 비어있습니다."),
    _PRODUCT_ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "USER400", "장바구니에 이미 해당상품이 존재합니다."),

    // Security 에러
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SEC4001", "잘못된 형식의 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SEC4010", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "SEC4011", "토큰이 만료되었습니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "SEC4012", "토큰이 위조되었거나 손상되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "SEC4030", "권한이 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "SEC4041", "토큰이 존재하지 않습니다."),
    TOKEN_ORGANIZATION_NOT_FOND(HttpStatus.UNAUTHORIZED, "SEC4042", "존재하지 않는 단체입니다."),
    INTERNAL_SECURITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5000", "인증 처리 중 서버 에러가 발생했습니다."),
    INTERNAL_TOKEN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5001", "토큰 처리 중 서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
