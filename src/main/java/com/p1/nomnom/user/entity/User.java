package com.p1.nomnom.user.entity;

import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "p_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role = UserRoleEnum.CUSTOMER; // 기본 권한을 CUSTOMER로 설정

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // Soft Delete 기능

    public User(String username, String email, String password, String phone, UserRoleEnum role, boolean isDeleted) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.isDeleted = isDeleted;
    }


    public void deleteUser() {
        this.isDeleted = true;
    }
  
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<Order> orders = new ArrayList<>();
}

