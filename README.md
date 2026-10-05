# Marketplace Hexagonal

Backend de un marketplace multivendedor construido con Spring Boot 4 y Java 21, siguiendo arquitectura hexagonal (puertos y adaptadores) de forma estricta.

*Read this in [English](README.en.md).*

No es un CRUD con carpetas bonitas. Es un marketplace real: varios vendedores comparten catálogo, un mismo pedido se parte en subórdenes independientes que cada tienda despacha a su ritmo, el stock se reserva mientras el carrito está vivo, y compradores y vendedores conversan en tiempo real antes y después de la compra.

---

## Índice

- [Por qué existe este proyecto](#por-qué-existe-este-proyecto)
- [Stack](#stack)
- [Arquitectura](#arquitectura)
- [Cómo funciona la aplicación](#cómo-funciona-la-aplicación)
  - [Identidad y acceso](#1-identidad-y-acceso)
  - [Catálogo de productos](#2-catálogo-de-productos)
  - [Vendedores y tiendas](#3-vendedores-y-tiendas)
