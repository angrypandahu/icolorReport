package icolorreport

import javassist.NotFoundException

import java.nio.file.AccessDeniedException

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        "/"(view: "/index")
        "/adminIndex"(view: "/adminIndex")
        "403"(controller: "errors", action: "error403")
        "500"(controller: "errors", action: "error500")
        "500"(controller: "errors", action: "error403",
                exception: AccessDeniedException)
        "500"(controller: "errors", action: "error403",
                exception: NotFoundException)

    }
}
