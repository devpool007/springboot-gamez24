package com.gamez24.backend.springboot_gamez24.controller

import com.gamez24.backend.springboot_gamez24.Article
import com.gamez24.backend.springboot_gamez24.ArticleRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

//Many classes can be in a file unlike Java
@RestController
@RequestMapping("/api/v1/articles")
class ArticleController(val repository: ArticleRepository) {

//    val articles = mutableListOf(
//        Article(
//            title = "My title",
//            content = "My content"
//        )
//    )

    @GetMapping
    fun articles() = repository.findAllByOrderByCreatedAtDesc()

    @GetMapping("/{slug}")
    fun articlesByTitle(@PathVariable slug : String) = repository.findBySlug(slug) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND)

    @PostMapping
    fun newArticle(@RequestBody article: Article): Article {
        article.id = null
        repository.save(article)
        return article
    }

    @PutMapping("/{slug}")
    fun updateArticle(@RequestBody article: Article, @PathVariable slug: String): Article {
        val existingArticle = repository.findBySlug(slug) ?: throw  ResponseStatusException(HttpStatus.NOT_FOUND)
        existingArticle.content = article.content;
        repository.save(existingArticle)
        return existingArticle
    }

    @DeleteMapping("/{slug}")
    fun deleteArticle(@PathVariable slug: String){
        val existingArticle = repository.findBySlug(slug) ?: throw  ResponseStatusException(HttpStatus.NOT_FOUND)
        repository.delete(existingArticle)
    }


}
