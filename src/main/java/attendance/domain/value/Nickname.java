package attendance.domain.value;

public final class Nickname {

  private final String value;

  public Nickname(String value) {
    this.value = value;
  }

  private String validate(String v) {
    if (v == null) {
      throw new IllegalArgumentException("닉네임은 한 글자 이상이어야 합니다.");
    }
    var t = v.trim();
    if (t.isEmpty()) {
      throw new IllegalArgumentException("닉네임은 공백이 될 수 없습니다.");
    }
    return t;
  }
  public String nickname() {
    return getValue();
  }
  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Nickname n)) return false;
    return value.equals(n.value);
  }
}