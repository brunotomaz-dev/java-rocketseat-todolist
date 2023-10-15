package rocketseat.java.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rocketseat.java.todolist.user.IUserRepository;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // get the servlet path
        var servletPath = request.getServletPath();

        // check if the request is for /tasks/
        if (servletPath.startsWith("/tasks/")) {

            // get the authorization header
            var authorization = request.getHeader("Authorization");

            // remove the "Basic" word and trim the spaces
            var user_pass = authorization.substring("Basic".length()).trim();

            // decode the string
            byte[] authDecoded = Base64.getDecoder().decode(user_pass);

            // convert the byte array to string
            var authString = new String(authDecoded);

            // split the string in two parts
            String[] authParts = authString.split(":");
            var userName = authParts[0];
            var pass = authParts[1];

            // find the user by username
            var user = this.userRepository.findByUsername(userName);

            if (user == null) {
                response.sendError(401, "Usuário sem autorização");
            } else {
                // verify the password
                var passVerify = BCrypt.verifyer().verify(pass.toCharArray(), user.getPassword());

                if (passVerify.verified) {
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401, "Usuário sem autorização");
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
