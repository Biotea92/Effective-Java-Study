package org.example.chapter12.example;

import java.io.*;
import java.util.Base64;

public class Member implements Serializable {
    private String name;
    private String email;
    private int age;

    public Member(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    // Getter 생략
    @Override
    public String toString() {
        return String.format("Member %s %s %s", name, email, age);
    }

    public static void main(String[] args) {
        Member member = new Member("김배민", "deliverykim@baemin.com", 25);
        byte[] serializedMember;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(member);
                // serializedMember -> 직렬화된 member 객체
                serializedMember = baos.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 바이트 배열로 생성된 직렬화 데이터를 base64로 변환
        System.out.println(Base64.getEncoder().encodeToString(serializedMember));


        // 역직렬화
        // 직렬화 예제에서 생성된 base64 데이터
        String base64Member = Base64.getEncoder().encodeToString(serializedMember);
        byte[] serializedMember2 = Base64.getDecoder().decode(base64Member);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedMember2)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                // 역직렬화된 Member 객체를 읽어온다.
                Object objectMember = ois.readObject();
                Member member2 = (Member) objectMember;
                System.out.println(member2);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}