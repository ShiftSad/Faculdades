import { Body, Controller, HttpException, HttpStatus, Post } from '@nestjs/common';
import { NloginService } from '../nlogin/nlogin.service';
import { LoginDto } from '../dto/nlogin.dto';
import { JwtService } from '@nestjs/jwt';

@Controller('auth')
export class AuthController {
    constructor(
        private nloginService: NloginService,
        private jwtService: JwtService,
    ) { }

    @Post('login')
    async login(@Body() loginDto: LoginDto) {
        try {
            const isPasswordCorrect = await this.nloginService.checkPassword(
                loginDto.username,
                loginDto.password,
            );

            if (isPasswordCorrect) {
                const payload = { 
                    username: loginDto.username,
                    sub: loginDto.username
                };

                const token = this.jwtService.sign(payload);
                
                return {
                    success: true,
                    message: "Authentication successful",
                    token,
                    expiresIn: 3600 // 60 minutes in seconds
                };
            }  else {
                throw new HttpException('Invalid credentials', HttpStatus.UNAUTHORIZED);
            }
        } catch (error) {
            console.error('Login error:', error.message);
            if (error instanceof HttpException) {
                throw error;
            }

            throw new HttpException('Internal server error', HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Post('validate')
    async logout(@Body() body: { token: string }) {
        try {
            const decoded = this.jwtService.verify(body.token);
            return {
                success: true,
                message: "Token is valid",
                data: decoded
            };
        } catch (error) {
            console.error('Token validation error:', error.message);
            throw new HttpException('Invalid token', HttpStatus.UNAUTHORIZED);
        }
    }
}
