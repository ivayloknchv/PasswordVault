package bg.sofia.uni.fmi.mjt.passwords.server.checker.dto;

import java.util.List;

public record PasswordCheckerDTO(List<PasswordCandidateDTO> candidates) {
}
