import java.time.Instant;

public record Job(String id, String title, String company, String location, int salary,
                  Instant postedAt, String status) {}
