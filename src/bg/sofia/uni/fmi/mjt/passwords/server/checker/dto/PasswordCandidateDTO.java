package bg.sofia.uni.fmi.mjt.passwords.server.checker.dto;

public record PasswordCandidateDTO(String sha256, boolean revealedInExposure, int exposureCount) {
}
