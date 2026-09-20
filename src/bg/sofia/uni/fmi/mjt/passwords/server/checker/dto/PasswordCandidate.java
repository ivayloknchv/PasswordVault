package bg.sofia.uni.fmi.mjt.passwords.server.checker.dto;

public record PasswordCandidate(String sha256, boolean revealedInExposure, int exposureCount) {
}
