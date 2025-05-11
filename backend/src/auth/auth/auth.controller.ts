import { Body, Controller, HttpException, HttpStatus, Post } from '@nestjs/common';
import { NloginService } from '../nlogin/nlogin.service';
import { LoginDto } from '../dto/nlogin.dto';

@Controller('auth')
export class AuthController {
    constructor(private nloginService: NloginService) { }

    @Post('login')
    async login(@Body() loginDto: LoginDto) {
        try {
            const isPasswordCorrect = await this.nloginService.checkPassword(
                loginDto.username,
                loginDto.password,
            );

            if (isPasswordCorrect) {
                return {
                    success: true,
                    message: "Authentication successful",
                }
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
}
